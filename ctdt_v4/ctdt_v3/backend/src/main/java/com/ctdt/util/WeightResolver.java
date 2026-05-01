package com.ctdt.util;
import org.springframework.stereotype.Component;
import java.math.BigDecimal; import java.util.Map;
@Component
public class WeightResolver {
    private static final Map<String,BigDecimal> W=Map.of(
        "java",new BigDecimal("1.0"),"js",new BigDecimal("1.0"),"ts",new BigDecimal("1.0"),
        "py",new BigDecimal("1.2"),"c",new BigDecimal("1.5"),"cpp",new BigDecimal("1.5"),
        "html",new BigDecimal("0.8"),"css",new BigDecimal("0.8"),"txt",new BigDecimal("0.5"));
    private static final BigDecimal DEF=new BigDecimal("1.0");
    public BigDecimal resolve(String fn){
        if(fn==null||!fn.contains("."))return DEF;
        return W.getOrDefault(fn.substring(fn.lastIndexOf('.')+1).toLowerCase(),DEF);
    }
}
