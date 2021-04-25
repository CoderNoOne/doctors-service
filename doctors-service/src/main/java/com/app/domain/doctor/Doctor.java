package com.app.domain.doctor;

import com.app.application.dto.DoctorDetails;
import com.app.application.dto.DoctorDto;
import com.app.domain.profession.Profession;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Getter
@Table(name = "doctors")
@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    private String username;

    @Setter
    private char[] password;

    @Setter
    @ManyToMany
    @JoinTable(
            name = "doctors_professions",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "profession_id", referencedColumnName = "id"))
    private List<Profession> professions;


    public DoctorDetails toDetails() {
        return DoctorDetails.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .professions(Objects.nonNull(professions) ? professions.stream().map(Profession::toDto).toList() : new ArrayList<>())
                .build();
    }

    public DoctorDto toDto() {
        return DoctorDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

}
