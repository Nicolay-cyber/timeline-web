package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.repositories.ParameterRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }
    public Parameter getParameterById(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
    }
    public Parameter getParameterByAbbreviation(String s) {
        return parameterRepository.findByAbbreviation(s);
    }

    /*public Parameter getParameter(Long id, Double step) {
        Parameter parameter = parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
        replaceVariables(parameter);
        return parameter;
    }

    private void replaceVariables(Parameter parameter) {

        List<Point> points = new ArrayList<Point>();
        Calculator calculator = new Calculator();
        List<String> expression = new ArrayList<String>();
        for(Function f : parameter.getFunctions()){
            expression = Arrays.asList(f.getExpression().split(" "));
            for(String s : expression){
                if(!s.matches("-?\\d+(\\.\\d+)?") && !s.matches("t")){
                    Parameter valuableParameter = parameterRepository.findByAbbreviation(s);
                    replaceVariables(valuableParameter);

                }
            }

        }
    }
*/
}
