package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.repositories.FunctionRepository;
import uni.nikdiu.timelineweb.repositories.ModelRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FunctionService {
    private final FunctionRepository functionRepository;

    public List<Function> getAllFunctionsWithParameter(Parameter parameter) {
        List<Function> functions = functionRepository.getAllByRelatedParameters(parameter);
        // Remove functions that are associated with the same parameter
        functions.removeIf(func -> parameter.getFunctions().contains(func));
        return functions;
    }

    public Function getFunctionById(Long id) {
        return functionRepository.findById(id).orElse(null);
    }

    public Function save(Function function) {
        return functionRepository.save(function);
    }

    public void remove(Function function) {
        System.out.println("Removed function: " + function);
        functionRepository.deleteById(function.getId());
    }

    public Function update(Function function) {
        System.out.println("Updating function: " + function);
        Function oldFunction = getFunctionById(function.getId());

        oldFunction.setStartPoint(function.getStartPoint());
        oldFunction.setEndPoint(function.getEndPoint());
        oldFunction.setStringExpression(function.getStringExpression());
        oldFunction.setRelatedParameters(function.getRelatedParameters());

        System.out.println("Updated function " + oldFunction);
        return save(oldFunction);


    }

}
