package uni.nikdiu.timelineweb.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long>, JpaSpecificationExecutor<Point> {
}
