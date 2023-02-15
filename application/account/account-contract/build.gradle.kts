dependencies {
    implementation(project(":application:shared"))
    implementation(project(":application:mediator"))
    implementation(project(":application:mongo-db-access"))
    implementation(project(":application:event-store:event-store-contract"))
    implementation(project(":application:security:security-contract"))
}
