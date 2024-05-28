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
        System.out.println("found parameters: " + parameterRepository.findAll());
        return parameterRepository.findAll();
    }

    public Parameter getParameterById(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
    }

    public Parameter getParameterByAbbreviation(String s) {
        return parameterRepository.findByAbbreviation(s);
    }

    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }
}
