package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Unit;
import uni.nikdiu.timelineweb.repositories.ModelRepository;
import uni.nikdiu.timelineweb.repositories.UnitRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    public Unit getUnitById(Long id)  {
        return unitRepository.findById(id).orElseThrow(() -> new RuntimeException("Unit not found"));
    }
    public Unit save(Unit unit) {
        return unitRepository.save(unit);
    }
}
