package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;
import uni.nikdiu.timelineweb.repositories.PointRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public List<Point> getAllPoints() {
        return pointRepository.findAll();
    }

    public Point getPointById(Long id) {
        return pointRepository.findById(id).orElse(null);
    }

    public Point save(Point point) {
        System.out.println("saved point: " + point);
        return pointRepository.save(point);
    }

    public void remove(Point point) {
        System.out.println("Removing point: " + point);
        pointRepository.deleteById(point.getId());

        // Проверка, была ли функция действительно удалена
        Optional<Point> removedPoint = pointRepository.findById(point.getId());
        if (removedPoint.isEmpty()) {
            System.out.println("Point is removed");
        } else {
            System.out.println("Point is not removed");
        }
    }


    public Point update(Point point) {
        System.out.println("Updating point: " + point);
        Point oldPoint = getPointById(point.getId());

        oldPoint.setX(point.getX());
        oldPoint.setY(point.getY());
        return save(oldPoint);
    }

}
