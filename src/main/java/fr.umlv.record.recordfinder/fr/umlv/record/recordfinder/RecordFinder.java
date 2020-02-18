package fr.umlv.record.recordfinder;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LRETURN;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class RecordFinder {
  private static String packaze(String className) {
    int index = className.lastIndexOf('/');
    if (index == -1) {
      return "";
    }
    return className.substring(0, index);
  }
  
  public static void main(String[] args) throws IOException {
    var javaBaseOnly = true;
    var recordSet = new LinkedHashSet<String>();
    
    var finder = ModuleFinder.ofSystem();
    for(var ref: finder.findAll()) {
      var descriptor = ref.descriptor();
      // only look in java.base
      if (javaBaseOnly && !descriptor.name().equals("java.base")) {
        continue;
      }
      
      var exportedPackages = descriptor.exports().stream().filter(export -> export.targets().isEmpty()).map(export -> export.source().replace('.', '/')).collect(toSet());
      
      //System.out.println("module " + descriptor.name() + " exported " + exportedPackages);
      
      try(var reader = ref.open()) {
        var filenames = reader.list().filter(f -> f.endsWith(".class")).collect(toList());
        for(var filename: filenames) {
          try(var input = reader.open(filename).orElseThrow()) {
            var classReader = new ClassReader(input);
            var className = classReader.getClassName();
            classReader.accept(new ClassVisitor(ASM7) {
              private boolean isAValidRecord;
              private final HashMap<String, Boolean> fieldMap = new HashMap<>();
              
              @Override
              public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                isAValidRecord = "java/lang/Object".equals(superName) && ((access & ACC_ABSTRACT) == 0) && !name.contains("$") &&
                    ((access & ACC_PUBLIC) == 0 || !exportedPackages.contains(packaze(name)));
              }
              @Override
              public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ((access & ACC_STATIC) != 0) {
                  return null;
                }
                if ((access & ACC_FINAL) == 0) {
                  isAValidRecord = false;
                  return null;
                }
                fieldMap.put(name, false);
                return null;
              }
              @Override
              public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (fieldMap.isEmpty()) {  // shortcut
                  return null;
                }
                if (descriptor.endsWith(")V") && !descriptor.startsWith("()")) {
                  return null;
                }
                return new MethodVisitor(ASM7) {
                  private boolean isGetter = true;
                  private String fieldName;
                  
                  @Override
                  public void visitInsn(int opcode) {
                    isGetter = isGetter && (opcode == IRETURN || opcode == LRETURN || opcode == FRETURN || opcode == DRETURN || opcode == ARETURN);
                  }
                  @Override
                  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                    if (!isGetter) {
                      return;
                    }
                    if (opcode == GETFIELD && owner.equals(className)) {
                      fieldName = name;
                      return;
                    }
                    isGetter = false;
                  }
                  @Override
                  public void visitVarInsn(int opcode, int var) {
                    isGetter = isGetter && opcode == ALOAD && var == 0;
                  }
                  @Override
                  public void visitEnd() {
                    if (isGetter) {
                      fieldMap.put(fieldName, true);
                    }
                  }
                };
              }
              
              @Override
              public void visitEnd() {
                if (isAValidRecord && !fieldMap.isEmpty() && fieldMap.values().stream().allMatch(v -> v)) {
                  recordSet.add(className);
                }
              }
            }, 0);
          }
        }
      }
    }
    
    //recordSet.stream().filter(name -> name.startsWith("java/")).forEach(System.out::println);
    //recordSet.stream().filter(name -> name.startsWith("jdk/")).forEach(System.out::println);
    recordSet.forEach(System.out::println);
  }
}
