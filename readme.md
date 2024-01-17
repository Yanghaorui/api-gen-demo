https://openapi-generator.tech/docs/plugins

Open API Generator是一个通过OpenAPI 2.0/3.x 文件生成代码，文档的工具。它支持多种语言，多种框架，不同的使用方式。

本篇主要介绍如何通过maven调用用Open API Generator来生成在spring-boot框架下的Java代码，以及修改模版和自定义Generator。

## 优点

1. 支持maven, Gradle, CLI 等多种使用方式，
2. 支持多种语言，
3. 可以修改模版，自定义生成器，非常灵活。

## 使用
- 在pom.xml中加入下面的plugin
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.6.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>api-docs.yaml</inputSpec>
                <!-- spring generator-->
                <generatorName>spring</generatorName>
                <output>${project.basedir}</output>
                <apiPackage>indi.haorui.api.service</apiPackage>
                <modelPackage>indi.haorui.api.dto</modelPackage>
                <!-- 生成一些额外的文件，比如README.md,pom.xml（会覆盖原本的）等， -->
                <generateSupportingFiles>false</generateSupportingFiles>
                <configOptions>
                    <library>spring-boot</library>
                    <hideGenerationTimestamp>true</hideGenerationTimestamp>
                    <sourceFolder>src//main/java</sourceFolder>
                    <skipDefaultInterface>true</skipDefaultInterface> <!-- 不生成接口的default方法 -->
                    <useSpringBoot3>true</useSpringBoot3><!-- 使用jakarta包校验，默认javax -->
                    <useTags>true</useTags> <!-- 是否通过tag 生成API 类名 -->
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
> library 支持 spring-boot , spring-cloud, spring-http-interface

- 加入下面的dependencies
```xml
<dependencies>
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.3.0</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.openapitools/jackson-databind-nullable -->
    <dependency>
        <groupId>org.openapitools</groupId>
        <artifactId>jackson-databind-nullable</artifactId>
        <version>0.2.6</version>
    </dependency>
</dependencies>
```
- 生成代码
```shell
mvn clean compile
```
对应例子 standard


## 模版修改

因为生成的代码手动修改的话，在后面compile也会被覆盖掉，所以如果生成的代码不满足我们需求，可以通过修改模版的方式修改代码。

> 模版是mustache写的。https://mustache.github.io/mustache.5.html

- 下载模版复制到工程目录
```shell
mkdir -p ~/.openapi-generator/templates/ && cd $_
curl -L https://api.github.com/repos/OpenAPITools/openapi-generator/tarball | tar xz
cp `ls`/modules/openapi-generator/src/main/resources/JavaSpring/ {project.basedir}/mustache/
```
- 在plugin中声明模版位置
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.6.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                ...
                <templateDirectory>./mustache</templateDirectory>
                ...
            </configuration>
        </execution>
    </executions>
</plugin>
```
对应例子 customized

## 自定义Generator
如果修改模版，还是不能满足对代码的需求，还可以通过自定义Generator来进一步修改代码。

- 生成Generator项目
```shell
cd ~/.openapi-generator/templates
cd `ls`
cd modules/openapi-generator-cli/
mvn clean package
cd target
java -jar openapi-generator-cli.jar meta -o ~/IdeaProjects/my-codegen -n my-codegen -p com.my.company.codegen
```
会生成项目my-codegen, 根据需求修改项目代码。

生成的项目修改后执行 `mvn clean install` 或 `mvn clean deploy` ，

pom.xml

坐标：
```xml
<dependency>
<groupId>org.openapitools</groupId>
<artifactId>my-codegen-openapi-generator</artifactId>
<version>1.0.0</version>
</dependency>
```
- 修改目标依赖
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.6.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                ...
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.openapitools</groupId>
            <artifactId>my-codegen-openapi-generator</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

> 注意：plugin中的 generatorName 要和Generator项目中的 getName方法保持一致！

对应例子membership-gen

## 引用
https://openapi-generator.tech/docs/plugins
https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/spring.md
https://mustache.github.io/mustache.5.html