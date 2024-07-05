package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelGraphDto {
    private List<Double> XValues = new ArrayList<>();
    private List<LineDto> YValues = new ArrayList<>();

    @Override
    public String toString() {
        return "ModelGraphDto:" +
                "\n     XValues: " + XValues +
                "\n     YValues: " + YValues
                ;
    }
}
