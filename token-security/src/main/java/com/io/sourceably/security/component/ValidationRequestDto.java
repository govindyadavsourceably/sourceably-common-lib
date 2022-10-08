package com.io.sourceably.security.component;

public interface ValidationRequestDto {
    @lombok.Data
    class ValidationRequest {
        private String gtin;
        private String pcbPartNumber;
        private String description;
    }

    @lombok.Data
    class ValidationResponse {
        private java.util.UUID id;
        private String gtin;
        private String pcbPartNumber;
        private String createdBy;
        private String description;


    }
}
