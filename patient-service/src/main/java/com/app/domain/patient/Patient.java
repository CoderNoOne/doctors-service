package com.app.domain.patient;

import com.app.domain.patient.embeddable.FavoriteDoctor;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Getter
@Table(schema = "reactive_db", name = "patients")
@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private Integer age;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "reactive_db", name = "favorite_doctors", joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"))
    private Set<FavoriteDoctor> favoriteDoctors;


}
