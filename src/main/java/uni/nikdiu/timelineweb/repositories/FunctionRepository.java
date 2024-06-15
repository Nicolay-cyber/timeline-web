package uni.nikdiu.timelineweb.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.Collection;
import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long>, JpaSpecificationExecutor<Function> {
    List<Function> getAllByRelatedParameters(Parameter parameter);
}
