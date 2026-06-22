package com.studioweb.studio_web.appointment;

import com.studioweb.studio_web.appointment.dto.AppointmentDtoRequest;
import com.studioweb.studio_web.appointment.dto.AppointmentDtoResponse;
import com.studioweb.studio_web.client.Client;
import com.studioweb.studio_web.client.ClientRepository;
import com.studioweb.studio_web.exception.AppointmentNotFoundException;
import com.studioweb.studio_web.exception.ClientNotFoundException;
import com.studioweb.studio_web.exception.UserNotFoundException;
import com.studioweb.studio_web.user.User;
import com.studioweb.studio_web.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ClientRepository clientRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public List<AppointmentDtoResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentDtoResponse getAppointmentById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public AppointmentDtoResponse insertAppointment(AppointmentDtoRequest dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.clientId()));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setUser(user);
        appointment.setDescricao(dto.descricao());
        appointment.setDataMarcada(dto.dataMarcada());
        appointment.setHoraInicio(dto.horaInicio());
        appointment.setStatus(AppointmentStatus.MARCADO);
        appointment.setCriadoEm(LocalDateTime.now());

        return toResponse(appointmentRepository.save(appointment));
    }

    public AppointmentDtoResponse updateAppointment(Long id, AppointmentDtoRequest dto) {
        Appointment appointment = findOrThrow(id);

        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.clientId()));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        appointment.setClient(client);
        appointment.setUser(user);
        appointment.setDescricao(dto.descricao());
        appointment.setDataMarcada(dto.dataMarcada());
        appointment.setHoraInicio(dto.horaInicio());

        return toResponse(appointmentRepository.save(appointment));
    }

    public AppointmentDtoResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findOrThrow(id);
        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException(id);
        }
        appointmentRepository.deleteById(id);
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private AppointmentDtoResponse toResponse(Appointment appointment) {
        return new AppointmentDtoResponse(
                appointment.getId(),
                appointment.getDataMarcada(),
                appointment.getHoraInicio(),
                appointment.getHoraFim(),
                appointment.getStatus(),
                appointment.getDescricao(),
                appointment.getClient().getId(),
                appointment.getUser().getId()
        );
    }
}
