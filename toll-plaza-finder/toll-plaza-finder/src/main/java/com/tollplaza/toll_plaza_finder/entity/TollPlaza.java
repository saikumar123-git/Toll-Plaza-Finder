
package com.tollplaza.toll_plaza_finder.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "toll_plazas",

        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_toll_name_coordinates",
                        columnNames = {
                                "name",
                                "latitude",
                                "longitude"
                        }
                )
        },

        indexes = {
                @Index(
                        name = "idx_toll_plaza_lat_lon",
                        columnList = "latitude, longitude"
                )
        }
)
public class TollPlaza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "geo_state")
    private String geoState;

    // Default constructor
    public TollPlaza() {
    }

    // Parameterized constructor
    public TollPlaza(
            String name,
            Double latitude,
            Double longitude,
            String geoState) {

        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoState = geoState;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getGeoState() {
        return geoState;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setGeoState(String geoState) {
        this.geoState = geoState;
    }
}
