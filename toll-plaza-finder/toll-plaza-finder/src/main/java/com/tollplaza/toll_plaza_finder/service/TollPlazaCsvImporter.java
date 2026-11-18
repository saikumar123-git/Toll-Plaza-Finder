
package com.tollplaza.toll_plaza_finder.service;

import com.tollplaza.toll_plaza_finder.entity.TollPlaza;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class TollPlazaCsvImporter implements CommandLineRunner {

    private final TollPlazaImportService importService;

    public TollPlazaCsvImporter(
            TollPlazaImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) {
        importCsvData();
    }

    public void importCsvData() {

        var resource = new ClassPathResource(
                "data/toll_plaza_india.csv"
        );

        if (!resource.exists()) {
            System.out.println("Toll plaza CSV file not found!");
            return;
        }

        List<TollPlaza> plazas = new ArrayList<>();
        int skippedRows = 0;

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (
                Reader reader = new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                );

                CSVParser parser = CSVParser.parse(reader, format)
        ) {

            Set<String> headers = parser.getHeaderMap().keySet();

            if (!headers.containsAll(Set.of(
                    "toll_name",
                    "latitude",
                    "longitude",
                    "geo_state"
            ))) {
                throw new IllegalStateException(
                        "CSV file is missing required columns"
                );
            }

            for (CSVRecord record : parser) {

                try {

                    String name = record.get("toll_name").trim();
                    String state = record.get("geo_state").trim();

                    if (name.isBlank()) {
                        skippedRows++;
                        continue;
                    }

                    double latitude = Double.parseDouble(
                            record.get("latitude").trim()
                    );

                    double longitude = Double.parseDouble(
                            record.get("longitude").trim()
                    );

                    if (!Double.isFinite(latitude)
                            || !Double.isFinite(longitude)) {
                        skippedRows++;
                        continue;
                    }

                    if (latitude < 6.0 || latitude > 38.0
                            || longitude < 68.0 || longitude > 98.0) {
                        skippedRows++;
                        continue;
                    }

                    plazas.add(new TollPlaza(
                            name,
                            latitude,
                            longitude,
                            state
                    ));

                } catch (IllegalArgumentException ex) {
                    skippedRows++;
                }
            }

            TollPlazaImportService.ImportResult result =
                    importService.importRecords(plazas);

            System.out.println("--------------------------------");
            System.out.println("Toll Plaza CSV Import Completed");
            System.out.println("--------------------------------");
            System.out.println("New records inserted: " + result.inserted());
            System.out.println("Existing records updated: " + result.updated());
            System.out.println("Duplicate rows skipped: " + result.duplicates());
            System.out.println("Invalid rows skipped: " + skippedRows);
            System.out.println("--------------------------------");

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Failed to import toll plaza CSV data",
                    ex
            );
        }
    }
}
