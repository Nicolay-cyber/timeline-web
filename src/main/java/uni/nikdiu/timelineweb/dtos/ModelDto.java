package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {
    private Long id;
    private String name;
    private String description;
}
