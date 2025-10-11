package co.com.assessment.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Builder(toBuilder=true)
public class UserEntity {

    private String id;

    private String email;

    private String firstName;
    private String lastName;

    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("Id")
    public String getId() {
        return id;
    }

    @DynamoDbSecondarySortKey(indexNames = {"Email"})
    @DynamoDbAttribute("Email")
    public String getEmail() {
        return email;
    }
    @DynamoDbAttribute("CreatedAt")

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @DynamoDbAttribute("FirstName")

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDbAttribute("LastName")

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
