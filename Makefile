
check:
	./gradlew clean mavenAndroidJavadocs check bintrayUpload

publish: check
	./gradlew -PdryRun=false --info bintrayUpload
	./gradlew releng
