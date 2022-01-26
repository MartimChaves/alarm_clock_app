/*
 * CONNECT TO OUTGOING PORT HC-05 'DEV-B' TO VIEW DATA ON SERIAL MONITOR
 * USE THIS SKETCH ONLY FOR VIEWING SENSOR DATA ON SERIAL MONITOR.....NOT FOR FILE WRITING
 */
int temp; //variable to hold temperature sensor value
long tm,t,d; //variables to record time in seconds

int x = 0;
int counter = 0;
char inChar = 'n';

void setup()
{
  Serial.begin(9600);
  pinMode(13,OUTPUT);//temperature sensor connected to analog 0
  analogReference(DEFAULT);
}

void loop()
{
  /*temp = analogRead(0); //analog reading temperature sensor values

  //required for converting time to seconds
  tm = millis();
  t = tm/1000;
  d = tm%1000;

  Serial.flush();

  //printing time in seconds
  Serial.print("time : ");
  Serial.print(t);
  Serial.print(".");
  Serial.print(d);
  Serial.print("s\t");

  //printing temperature sensor values
  Serial.print("temperature : ");
  Serial.print(temp);*/
  if(x==0){
    Serial.print(1);
    x = 1;  
  }

  while(Serial.available() > 0){
    char inChar = Serial.read();  
  }

  if (inChar = 'y'){
    digitalWrite(13,HIGH);
    delay(100);
    digitalWrite(13,LOW);
    delay(100);
    digitalWrite(13,HIGH);
    delay(100);
    digitalWrite(13,LOW);
    delay(100);
    digitalWrite(13,HIGH);
    inChar = 'n';
    }
    
  delay(1000);
    
  if (x == 1){
    Serial.print("-");
    x = 0;
  }

  while(Serial.available() > 0){
    char inChar = Serial.read();  
  }

  if (inChar = 'y'){
    digitalWrite(13,LOW);
    delay(100);
    digitalWrite(13,HIGH);
    delay(100);
    digitalWrite(13,LOW);
    delay(100);
    digitalWrite(13,HIGH);
    delay(100);
    digitalWrite(13,LOW);
    inChar = 'n';
    }
  
  delay(2000);

  counter++;

  if (counter > 5){
    Serial.print("-----END OF 18 SECONDS-----");  
    counter = 0;
  }
  Serial.read();
  delay(5000);
  
  /*delay(200);//delay of 2 seconds*/
}

