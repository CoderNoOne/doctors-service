package com.app.domain.profession;

import com.app.application.dto.ProfessionDetailsDto;
import com.app.application.dto.ProfessionDto;
import com.app.domain.doctor.Doctor;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;


@ToString
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Table(name = "professions")
@Entity
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "professions")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Doctor> doctors;

    public ProfessionDto toDto() {
        return ProfessionDto.builder()
                .id(id)
                .name(name)
                .build();

    }

    public ProfessionDetailsDto toDetails(){
        return ProfessionDetailsDto.builder()
                .id(id)
                .name(name)
                .doctors(doctors != null ? doctors.stream().map(Doctor::toDto).toList() : new ArrayList<>())
                .build();
    }
}
