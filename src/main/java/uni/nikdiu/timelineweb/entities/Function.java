package uni.nikdiu.timelineweb.entities;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "functions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_point")
    private Double startPoint;

    @Column(name = "end_point")
    private Double endPoint;

    @Column(name = "expression")
    private String stringExpression;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_parameter_id")
    private Parameter parent_parameter_id;

    @Transient
    private List<String> expression;

    @PostConstruct
    private void extractExpression() {
        expression = Arrays.asList(stringExpression.split(" "));
    }
/*
    @OneToMany(mappedBy = "function", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Parameter> relatedParameters;


*/
}
