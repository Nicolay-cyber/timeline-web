package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.nikdiu.timelineweb.convectors.ModelConvector;
import uni.nikdiu.timelineweb.convectors.UnitConvector;
import uni.nikdiu.timelineweb.dtos.ModelDto;
import uni.nikdiu.timelineweb.dtos.UnitDto;
import uni.nikdiu.timelineweb.services.ModelService;
import uni.nikdiu.timelineweb.services.UnitService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitService unitService;


    private final UnitConvector unitConvector;

    @GetMapping
    public List<UnitDto> getAllModels() {
        return unitService.getAllUnits()
                .stream()
                .map(model -> unitConvector.toDto(model))
                .collect(Collectors.toList());
    }
}
