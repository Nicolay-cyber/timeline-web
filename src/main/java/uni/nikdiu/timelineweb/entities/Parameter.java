package uni.nikdiu.timelineweb.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "abbreviation")
    private String abbreviation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @OneToMany(mappedBy = "parentParameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Function> functions;

    @OneToMany(mappedBy = "parentParameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Point> points;

    public Parameter(Long id, String name, String description, String abbreviation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.abbreviation = abbreviation;
        this.points = new ArrayList<>();
        this.functions = new ArrayList<>();
    }
    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", unit=" + (unit != null ? unit.getName() : null) +  // Example using unit name
                '}';
    }
}
