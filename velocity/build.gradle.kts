dependencies {
    compileOnly("com.velocitypowered:velocity-api:1.0.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:1.0.0-SNAPSHOT")
    implementation(project(":nuvotifier-api"))
    implementation(project(":nuvotifier-common"))
}