package org.e2e.e2e.ride.domain;

import com.uber.h3core.H3Core;

import org.e2e.e2e.auth.utils.AuthorizationUtils;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.coordinate.dto.CoordinateDto;
import org.e2e.e2e.driver.domain.Driver;
import org.e2e.e2e.driver.dto.DriverResponseDto;
import org.e2e.e2e.driver.infrastructure.DriverRepository;
import org.e2e.e2e.exceptions.ResourceNotFoundException;
import org.e2e.e2e.passenger.domain.Passenger;
import org.e2e.e2e.passenger.dto.PassengerResponseDTO;
import org.e2e.e2e.passenger.exceptions.UnauthorizeOperationException;
import org.e2e.e2e.passenger.infrastructure.PassengerRepository;
import org.e2e.e2e.ride.dto.CreateRideRequestDto;
import org.e2e.e2e.ride.dto.RideInfoResponseDto;
import org.e2e.e2e.ride.dto.BasicRideResponseDto;
import org.e2e.e2e.ride.dto.RidesByUserDto;
import org.e2e.e2e.ride.infrastructure.RideRepository;
import org.e2e.e2e.vehicle.dto.VehicleBasicDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    AuthorizationUtils authorizationUtils;

    @Autowired
    public RideService(RideRepository rideRepository, PassengerRepository passengerRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
    }

    public BasicRideResponseDto createRide(CreateRideRequestDto rideRequest) {
        String userEmail = authorizationUtils.getCurrentUserEmail();

        if (userEmail == null) {
            throw new UnauthorizeOperationException("Anonymous User not allowed to access this resource");
        }

        Passenger ridePassenger = passengerRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Passenger not found"));

        Ride ride = new Ride();
        ride.setOriginName(rideRequest.getOriginName());
        ride.setDestinationName(rideRequest.getDestinationName());
        ride.setPrice(rideRequest.getPrice());
        ride.setOriginCoordinates(rideRequest.getOriginCoordinates());
        ride.setDestinationCoordinates(rideRequest.getDestinationCoordinates());
        ride.setStatus(Status.valueOf("REQUESTED"));
        ride.setPassenger(ridePassenger);

        rideRepository.save(ride);

        H3Core h3;
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String newHexAddress = h3.latLngToCellAddress(
                ride.getOriginCoordinates().getLatitude(),
                ride.getOriginCoordinates().getLongitude(),
                10);

        BasicRideResponseDto response = new BasicRideResponseDto();
        response.setId(ride.getId());
        response.setOriginName(ride.getOriginName());
        response.setDestinationName(ride.getDestinationName());
        response.setHexAddress(newHexAddress);
        response.setStatus(Status.REQUESTED.name());

        return response;
    }

    public RideInfoResponseDto assignRide(Long rideId) {

        String userEmail = authorizationUtils.getCurrentUserEmail();

        if (userEmail == null) {
            throw new UnauthorizeOperationException("Anonymous User not allowed to access this resource");
        }

        Driver driver = driverRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Driver not found"));

        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        ride.setStatus(Status.ACCEPTED);
        ride.setDriver(driver);

        rideRepository.save(ride);

        RideInfoResponseDto response = new RideInfoResponseDto();
        response.setRideId(rideId);
        response.setStatus(Status.ACCEPTED.name());
        response.setOriginName(ride.getOriginName());
        response.setDestinationName(ride.getDestinationName());
        response.setOriginCoordinates(ride.getOriginCoordinates());
        response.setDestinationCoordinates(ride.getDestinationCoordinates());

        response.setDriver(modelMapper.map(driver, DriverResponseDto.class));
        response.setPassenger(modelMapper.map(ride.getPassenger(), PassengerResponseDTO.class));

        return response;
    }

    public BasicRideResponseDto updateRideStatus(Long rideId, String status) {
        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

        ride.setStatus(Status.valueOf(status));
        rideRepository.save(ride);

        return modelMapper.map(ride, BasicRideResponseDto.class);
    }

    public void cancelRide(Long rideId) {

        String userEmail = authorizationUtils.getCurrentUserEmail();

        if (userEmail == null) {
            throw new UnauthorizeOperationException("Anonymous User not allowed to access this resource");
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

        if (List.of("IN_PROGRESS", "COMPLETED").contains(ride.getStatus().name())) {
            throw new IllegalArgumentException("Ride is not available for cancellation");
        }

        ride.setStatus(Status.CANCELLED);
        rideRepository.save(ride);
    }

    public Page<RidesByUserDto> getRidesByUser(int page, int size) {
        String userEmail = authorizationUtils.getCurrentUserEmail();

        if (userEmail == null) {
            throw new UnauthorizeOperationException("Anonymous User not allowed to access this resource");
        }

        Passenger passenger = passengerRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Passenger not found"));
        Pageable pageable = PageRequest.of(page, size);

        Page<Ride> rides = rideRepository.findAllByPassengerIdAndStatus(passenger.getId(), Status.COMPLETED, pageable);
        return rides.map(ride -> modelMapper.map(ride, RidesByUserDto.class));
    }

    public RideInfoResponseDto getRideInfo(Long id, String name) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

        RideInfoResponseDto response = new RideInfoResponseDto();
        response.setOriginName(ride.getOriginName());
        response.setDestinationName(ride.getDestinationName());
        response.setOriginCoordinates(ride.getOriginCoordinates());
        response.setDestinationCoordinates(ride.getDestinationCoordinates());
        response.setStatus(ride.getStatus().name());
        response.setPassenger(modelMapper.map(ride.getPassenger(), PassengerResponseDTO.class));
        response.setDriver(modelMapper.map(ride.getDriver(), DriverResponseDto.class));

        return response;
    }
}
