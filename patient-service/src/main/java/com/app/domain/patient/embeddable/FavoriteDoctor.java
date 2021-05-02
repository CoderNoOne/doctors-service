package com.app.domain.patient.embeddable;

import javax.persistence.Embeddable;

@Embeddable
public class FavoriteDoctor {

    private Long id;

    private String username;
    private String firstName;
    private String lastName;
}
