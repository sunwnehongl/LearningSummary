# Spring Boot 介绍
## Spring Boot的优点
1. 快速创建独立运行的Spring项目以及以及与主流框架集成。
2. 使用嵌入式的Servlet容器，应用无需打成WAR包。
3. starters自动依赖和版本控制。
4. 大量的自动化配置，简化开发，也可以修改默认配置。
5. 无需xml配置，无需打码生成，开箱即用。
6. 生产环境上的运行时应用监控。
7. 与云计算的天然集成。

## Spring Boot Starters
Spring Boot 把常用的一些功能场景抽取成了一个个starters(启动器)，只需要在项目里引入这些starters的依赖，相关场景的所有依赖就导入了。简化了依赖的导入，和版本控制。例如：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.1.RELEASE</version>
  </parent>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
```

## 程序入口
Spring Boot 需要使用注解@SpringBootApplication来标识一个Spring Boot的主程序。例如代码：

```java
@SpringBootApplication
public class FinancialSystem {

    public static void main(String[] args) {
        SpringApplication.run(FinancialSystem.class, args);
    }
}
```

其中的注解@SpringBootApplication是一个组合注解包含如下注解：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```
@SpringBootConfiguration： 标识是Spring Boot 的配置类。
@EnableAutoConfiguration：开启自动配置功能。将主类所在的包下，以及子目录下的所有组件都自动配置到Spring容器中，并给容器中导入很多自动配置类。

