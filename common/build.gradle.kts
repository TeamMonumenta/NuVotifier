dependencies {
    implementation(project(":nuvotifier-api"))
    implementation("io.netty:netty-handler:4.1.49.Final")
    implementation("io.netty:netty-transport-native-epoll:4.1.49.Final:linux-x86_64")
    implementation("com.google.code.gson:gson:2.8.0")
    testImplementation("org.json:json:20180130") // retain this for testing reasons
    testImplementation("com.google.guava:guava:28.1-jre")
}
