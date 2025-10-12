package co.com.assessment.dynamodb;

import co.com.assessment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class DynamoDBTemplateAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private DynamoDbAsyncTable<UserEntity> customerTable;

    private DynamoDBTemplateAdapter dynamoDBTemplateAdapter;

    @BeforeEach
    void setUp() {
        when(dynamoDbEnhancedAsyncClient.table("Users", TableSchema.fromBean(UserEntity.class)))
                .thenReturn(customerTable);
        dynamoDBTemplateAdapter = new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

    }

    @Test
    void shouldSaveUser() {
        User model = User.builder()
                .id("c1e289dc-80ad-4920-be34-8df240053771")
                .email("noah@example.com")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id("c1e289dc-80ad-4920-be34-8df240053771")
                .email("noah@example.com")
                .build();

        when(customerTable.putItem(userEntity)).thenReturn(CompletableFuture.completedFuture(null));
        when(mapper.map(model, UserEntity.class)).thenReturn(userEntity);


        dynamoDBTemplateAdapter.saveUser(model)
                        .as(StepVerifier::create)
                                .assertNext(savedUser ->{
                                    assertNotNull(savedUser);
                                    assertNotNull(savedUser.getId());
                                }).verifyComplete();

        verify(customerTable).putItem(any(UserEntity.class));
    }

    @Test
    void shouldMapUserEntityToUserModel(){
        User userModel = User.builder()
                .id("c1e289dc-80ad-4920-be34-8df240053771")
                .email("noah@example.com")
                .password("Specter12")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_USER")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id("c1e289dc-80ad-4920-be34-8df240053771")
                .email("noah@example.com")
                .firstName("Noah")
                .lastName("Sebastian")
                .build();

        when(mapper.map(userModel, UserEntity.class)).thenReturn(userEntity);

        UserEntity savedUser = dynamoDBTemplateAdapter.toEntity(userModel);

        assertEquals(userModel.getId(), savedUser.getId());
        assertEquals(userModel.getEmail(), savedUser.getEmail());
        assertEquals(userModel.getFirstName(), savedUser.getFirstName());
        assertEquals(userModel.getLastName(), savedUser.getLastName());
        assertNotNull(savedUser.getCreatedAt());
    }
}