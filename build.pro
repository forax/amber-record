import static com.github.forax.pro.Pro.*;
import static com.github.forax.pro.builder.Builders.*;

resolver.
    checkForUpdate(true).
    dependencies(
        // JUnit 5
        "org.junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.7.0",
        "org.junit.platform.commons=org.junit.platform:junit-platform-commons:1.7.0",
        "org.apiguardian.api=org.apiguardian:apiguardian-api:1.1.0",
        "org.opentest4j=org.opentest4j:opentest4j:1.2.0",
        
        // ASM 9
        "org.objectweb.asm=org.ow2.asm:asm:9.0",
        
        // JMH
        "org.openjdk.jmh=org.openjdk.jmh:jmh-core:1.23",
        "org.apache.commons.math3=org.apache.commons:commons-math3:3.6.1",
        "net.sf.jopt-simple=net.sf.jopt-simple:jopt-simple:4.6",
        "org.openjdk.jmh.generator=org.openjdk.jmh:jmh-generator-annprocess:1.23"
    )

compiler.
    sourceRelease(16).
    enablePreview(true).
    processorModuleTestPath(path("deps")) // enable JMH annotation processor
      
packager.
    modules(
        "fr.umlv.record@1.0/fr.umlv.sealed.main.Main"
    )   
   
runner.
    enablePreview(true)
    
run(resolver, modulefixer, compiler, tester, packager, runner /*, perfer */)

/exit errorCode()
