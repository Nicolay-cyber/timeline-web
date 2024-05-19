package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.entities.Function;

@Component
@NoArgsConstructor
public class FunctionConvector {
    MathToLaTeXConverter latexConvector = new MathToLaTeXConverter();
    public FunctionDto toDto(Function function){

        return new FunctionDto(
                function.getId(),
                function.getStartPoint(),
                function.getEndPoint(),
                function.getParentParameter().getName(),
                latexConvector.toLaTeX(function.getStringExpression()));
    }
}
