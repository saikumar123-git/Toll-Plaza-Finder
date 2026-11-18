
package com.tollplaza.toll_plaza_finder.service;

import com.tollplaza.toll_plaza_finder.entity.TollPlaza;
import com.tollplaza.toll_plaza_finder.repository.TollPlazaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TollPlazaImportService {

    private static final int BATCH_SIZE = 500;

    private final TollPlazaRepository repository;

    public TollPlazaImportService(TollPlazaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImportResult importRecords(List<TollPlaza> plazas) {

        Map<TollPlazaKey, TollPlaza> existingRecords = new HashMap<>();

        for (TollPlaza plaza : repository.findAll()) {
            existingRecords.put(createKey(plaza), plaza);
        }

        Set<TollPlazaKey> processed = new HashSet<>();
        List<TollPlaza> batch = new ArrayList<>();

        int inserted = 0;
        int updated = 0;
        int duplicates = 0;

        for (TollPlaza plaza : plazas) {

            TollPlazaKey key = createKey(plaza);

            if (!processed.add(key)) {
                duplicates++;
                continue;
            }

            TollPlaza existing = existingRecords.get(key);

            if (existing != null) {

                if (!Objects.equals(
                        existing.getGeoState(),
                        plaza.getGeoState())) {

                    existing.setGeoState(plaza.getGeoState());
                    updated++;
                } else {
                    duplicates++;
                }

            } else {

                batch.add(plaza);
                existingRecords.put(key, plaza);
                inserted++;
            }

            if (batch.size() >= BATCH_SIZE) {
                repository.saveAll(batch);
                repository.flush();
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            repository.saveAll(batch);
            repository.flush();
        }

        return new ImportResult(inserted, updated, duplicates);
    }

    private TollPlazaKey createKey(TollPlaza plaza) {
        return new TollPlazaKey(
                plaza.getName().trim().toLowerCase(Locale.ROOT),
                plaza.getLatitude(),
                plaza.getLongitude()
        );
    }

    private record TollPlazaKey(
            String name,
            Double latitude,
            Double longitude
    ) {}

    public record ImportResult(
            int inserted,
            int updated,
            int duplicates
    ) {}
}
