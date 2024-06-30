package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;
import uni.nikdiu.timelineweb.repositories.ParameterRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;
    private final ModelService modelService;
    private final FunctionService functionService;
    private final PointService pointService;

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    public Parameter getParameterById(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
    }

    public Parameter getParameterByTag(String tag) {
        return parameterRepository.getParameterByTag(tag).orElseThrow(() -> new RuntimeException("Parameter " + tag + " not found"));
    }

    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Transactional
    public void deleteParameter(Long id) {
        Parameter parameter = getParameterById(id);
        List<Model> models = modelService.getAllModelsWithParameter(parameter).stream().map(object -> (Model) object).toList();
        //remove the parameter from all models
        for (Model model : models) {
            model.getParameters().remove(parameter);
        }
        //update the edited models
        for (Model model : models) {
            modelService.save(model);
        }
        //collect all functions which depend on this parameter
        List<Function> functions = functionService.getAllFunctionsWithParameter(parameter);

        //remove collected functions
        for (Function function : functions) {
            functionService.remove(function);
        }
        parameterRepository.delete(parameter);
        Optional<Parameter> removedParameter = parameterRepository.findById(parameter.getId());
        if (removedParameter.isEmpty()) {
            System.out.println("Parameter removed");
        } else {
            System.out.println("Parameter is not removed");
        }
    }

    @Transactional
    public Parameter updateParameter(Long id, Parameter updatedParameter) {
        System.out.println("Entity for updating:\n" + updatedParameter);

        Parameter parameter = getParameterById(id);

        parameter.setName(updatedParameter.getName());
        parameter.setAbbreviation(updatedParameter.getAbbreviation());
        parameter.setDescription(updatedParameter.getDescription());
        parameter.setUnit(updatedParameter.getUnit());

        updateFunctionList(updatedParameter, parameter);

        updatePointList(updatedParameter, parameter);

        return parameterRepository.save(parameter);
    }

    private void updateFunctionList(Parameter updatedParameter, Parameter parameter) {
        // Создаем изменяемую копию существующих функций
        List<Function> existingFunctions = new ArrayList<>(parameter.getFunctions());
        // Обновленные функции
        List<Function> updatedFunctions = updatedParameter.getFunctions();

        // Найти функции, которые удалены при обновлении
        List<Function> functionsToRemove = existingFunctions.stream()
                .filter(existingFunction -> updatedFunctions.stream().noneMatch(updatedFunction -> updatedFunction.getId().equals(existingFunction.getId())))
                .collect(Collectors.toList());

        // Удалить функции, которые больше не присутствуют
        for (Function function : functionsToRemove) {
            functionService.remove(function);
        }

        // Сохранить или обновить оставшиеся функции
        updatedFunctions.forEach(function -> {
            if (function.getId() == null) {
                functionService.save(function);
            } else {
                functionService.update(function);
            }
        });

        parameter.setFunctions(new ArrayList<>(updatedFunctions));
    }

    private void updatePointList(Parameter updatedParameter, Parameter parameter) {
        // Создаем изменяемую копию существующих points
        List<Point> existingPoints = new ArrayList<>(parameter.getPoints());
        // Обновленные points
        List<Point> updatedPoints = updatedParameter.getPoints();

        // Найти points, которые удалены при обновлении
        List<Point> pointsToRemove = existingPoints.stream()
                .filter(existingPoint -> updatedPoints.stream().noneMatch(updatedPoint -> updatedPoint.getId().equals(existingPoint.getId())))
                .collect(Collectors.toList());

        // Удалить points, которые больше не присутствуют
        for (Point point : pointsToRemove) {
            pointService.remove(point);
        }

        // Сохранить или обновить оставшиеся points
        updatedPoints.forEach(point -> {
            if (point.getId() == null) {
                pointService.save(point);
            } else {
                pointService.update(point);
            }
        });

        parameter.setPoints(new ArrayList<>(updatedPoints));
    }
}
