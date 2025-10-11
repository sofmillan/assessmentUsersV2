package co.com.assessment.dynamodb;

import co.com.assessment.dynamodb.helper.TemplateAdapterOperations;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.UserPersistenceGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.time.LocalDateTime;


@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<User, String, UserEntity> implements UserPersistenceGateway {

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**because you are not subscribing to the Mono<User> returned by the registerUser method
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), "Users");
    }

    @Override
    public Mono<User> saveUser(User user) {
        return this.save(user);
    }

    @Override
    protected UserEntity toEntity(User model) {
        var entity = super.toEntity(model);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
