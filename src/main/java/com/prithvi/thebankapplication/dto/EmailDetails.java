package com.prithvi.thebankapplication.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {
    @NonNull //added by me
    private String receipent;
    private String messageBody;
    private String subject;
    private String attachement;
}
