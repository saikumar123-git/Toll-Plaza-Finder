
package com.tollplaza.toll_plaza_finder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TollPlazaRequest(

        @NotBlank(message = "Source pincode is required")
        @Pattern(regexp = "^[1-9][0-9]{5}$",
                message = "Invalid source pincode")
        String sourcePincode,

        @NotBlank(message = "Destination pincode is required")
        @Pattern(regexp = "^[1-9][0-9]{5}$",
                message = "Invalid destination pincode")
        String destinationPincode

) {
}
