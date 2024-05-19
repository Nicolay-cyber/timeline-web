package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LaTeXConvector {
    public String toLaTeX(String expression){
        
        return "\\(" + expression + "\\)";
    }
}
