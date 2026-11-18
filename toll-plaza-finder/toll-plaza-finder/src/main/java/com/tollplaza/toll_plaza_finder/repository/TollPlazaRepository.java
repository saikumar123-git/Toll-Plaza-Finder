
package com.tollplaza.toll_plaza_finder.repository;

import com.tollplaza.toll_plaza_finder.entity.TollPlaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TollPlazaRepository
        extends JpaRepository<TollPlaza, Long> {

    List<TollPlaza> findByLatitudeBetweenAndLongitudeBetween(
            double minLatitude,
            double maxLatitude,
            double minLongitude,
            double maxLongitude
    );

    Optional<TollPlaza> findByNameAndLatitudeAndLongitude(
            String name,
            Double latitude,
            Double longitude
    );
}
