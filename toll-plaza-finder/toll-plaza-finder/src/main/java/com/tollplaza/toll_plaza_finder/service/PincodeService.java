
package com.tollplaza.toll_plaza_finder.service;

import org.springframework.stereotype.Service;

@Service
public class PincodeService {

    public double[] getCoordinates(String pincode) {

        if (pincode == null || !pincode.matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "Invalid Indian pincode: " + pincode
            );
        }

        switch (pincode) {

            case "560064":
                return new double[]{13.1155, 77.6070};

            case "411045":
                return new double[]{18.5590, 73.7868};

            case "110001":
                return new double[]{28.6328, 77.2197};

            default:
                throw new IllegalArgumentException(
                        "Coordinates not available for pincode: " + pincode
                );
        }
    }
}
