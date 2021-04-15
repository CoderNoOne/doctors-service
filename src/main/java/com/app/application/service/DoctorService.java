package com.app.application.service;

import com.app.application.dto.CreateDoctorDto;
import com.app.application.dto.DoctorDetails;
import com.app.application.dto.DoctorDto;
import com.app.application.dto.ProfessionDto;
import com.app.domain.doctor.Doctor;
import com.app.domain.doctor.DoctorRepository;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final Stage.SessionFactory sessionFactory;
    private final ProfessionRepository professionRepository;

    public Mono<DoctorDto> saveDoctor(CreateDoctorDto createDoctorDto) {

        return doctorRepository.addOrUpdate(createDoctorDto.toEntity())
                .map(Doctor::toDto);
    }

    public Mono<DoctorDto> getDoctorByIdWithFetchedProfessions(Long id) {

        return Mono.fromCompletionStage(sessionFactory.withStatelessSession(
                session -> session
                        .get(Doctor.class, id)
                        .thenCompose(doctor -> session
                                .fetch(doctor.getProfessions())
                                .thenApply(professions -> doctor.toDto()))));

    }

    public Flux<ProfessionDto> getDoctorProfessionsByDoctorId(Long id) {

        return Mono.fromCompletionStage(sessionFactory.withStatelessSession(
                session -> session
                        .createQuery("select d from Doctor d join fetch d.professions where d.id = :id", Doctor.class)
                        .setParameter("id", id)
                        .getSingleResult()))
                .map(Doctor::getProfessions)
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDto);


//        return Mono.fromCompletionStage(sessionFactory.withStatelessSession(
//                session -> session
//                        .get(Doctor.class, id)
//                        .thenCompose(doctor -> session.fetch(doctor.getProfessions())
//                        )))
//                .flatMapMany(Flux::fromIterable)
//                .map(Profession::toDto);
    }

    public Flux<DoctorDto> saveDoctors(List<CreateDoctorDto> createDoctorDtoList) {

        final List<String> professions = createDoctorDtoList.stream()
                .flatMap(createDoctorDto -> createDoctorDto.getProfessions().stream())
                .map(ProfessionDto::getName)
                .distinct()
                .toList();

//        professionRepository.findAllByNames(professions)

        return doctorRepository
                .addOrUpdateMany(createDoctorDtoList
                        .stream()
                        .map(CreateDoctorDto::toEntity)
                        .toList())
                .map(Doctor::toDto);


    }
}
