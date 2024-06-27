package uni.nikdiu.timelineweb.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long>, JpaSpecificationExecutor<Parameter> {
    Collection<Object> getAllByRelatedFunctions(Function function);
    Optional<Parameter> getParameterByTag(String tag);
}
