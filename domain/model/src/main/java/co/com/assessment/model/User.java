package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
}