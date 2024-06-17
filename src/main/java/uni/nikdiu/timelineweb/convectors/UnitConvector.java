package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.UnitDto;
import uni.nikdiu.timelineweb.entities.Unit;

@Component
@NoArgsConstructor
public class UnitConvector {
    // Convert Unit entity to UnitDto
    public UnitDto toDto(Unit unit) {
        if (unit == null) {
            return null;
        }

        return new UnitDto(
                unit.getId(),
                unit.getName(),
                unit.getAbbreviation()
        );
    }

    // Convert UnitDto to Unit entity
    public Unit toEntity(UnitDto unitDto) {
        if (unitDto == null) {
            return null;
        }

        return new Unit(
                unitDto.getId(),
                unitDto.getName(),
                unitDto.getAbbreviation()
        );
    }
}
