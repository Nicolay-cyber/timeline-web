package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.entities.Function;

import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class FunctionConvector {
    ExpressionTypeConverter latexConvector = new ExpressionTypeConverter();
    public FunctionDto toDto(Function function){

        return new FunctionDto(
                function.getId(),
                function.getStartPoint(),
                function.getEndPoint(),
                function.getParentParameter().getName(),
                latexConvector.classicToLatex(function.getStringExpression()));
    }

//    public Function toEntity(FunctionDto functionDto){
//        List<String> expressions = Arrays.asList(functionDto.getExpression().split(","));
//        return new Function(
//                functionDto.getId(),
//                functionDto.getStartPoint(),
//                functionDto.getEndPoint(),
//                functionDto.getExpression(),
//                functionDto.getParentParameter(),
//                functionDto.getRelatedParameters(),
//                expressions
//        );
//    }
}
