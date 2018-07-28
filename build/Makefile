default: pre-build
	cat stub.sh bankapp/target/bankapp-1.0-SNAPSHOT-jar-with-dependencies.jar > bank.out && chmod +x bank.out
	cat stub.sh atmapp/target/atmapp-1.0-SNAPSHOT-jar-with-dependencies.jar > atm.out && chmod +x atm.out
	chmod +x bank
	chmod +x atm

pre-build:
	rm -f -- *.out
	mvn -U clean install -gs common/settings.xml



