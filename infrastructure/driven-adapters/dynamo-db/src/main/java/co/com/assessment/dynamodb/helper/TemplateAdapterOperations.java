package co.com.assessment.dynamodb.helper;

import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;

public abstract class TemplateAdapterOperations<E, K, V> {
    private final Class<V> dataClass;
    private final Function<V, E> toEntityFn;
    protected ObjectMapper mapper;
    private final DynamoDbAsyncTable<V> table;
    private final DynamoDbAsyncIndex<V> tableByIndex;

    @SuppressWarnings("unchecked")
    protected TemplateAdapterOperations(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                        ObjectMapper mapper,
                                        Function<V, E> toEntityFn,
                                        String tableName,
                                        String... index) {
        this.toEntityFn = toEntityFn;
        this.mapper = mapper;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<V>) genericSuperclass.getActualTypeArguments()[2];
        table = dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
        tableByIndex = index.length > 0 ? table.index(index[0]) : null;
    }

    public Mono<E> save(E model) {
        return Mono.fromFuture(table.putItem(toEntity(model))).thenReturn(model);
    }
    protected V toEntity(E model) {
        return mapper.map(model, dataClass);
    }

    protected E toModel(V data) {
        return data != null ? toEntityFn.apply(data) : null;
    }
}