#define EOG_H A3
#define EOG_V A4
#define HI_THRESHOLD 50 //820 - 50 Y:368

bool count_horizontal_flag = false;
bool count_vertical_flag = false;
bool signalReceived = true;
int n1 = 0; //will be used to store the count
int n2 = 0;
unsigned long interval = 7000;
unsigned long previousMillis = 0;
int sum = 0;
char val;

void setup()
{
  Serial.begin(9600);
}

void loop()
{

  unsigned long currentMillis = millis(); // grab current time


  // check if "interval" time has passed (1000 milliseconds)


  int analog_in_H = analogRead(EOG_H);
  int analog_in_V = analogRead(EOG_V);



  if (analog_in_V > HI_THRESHOLD && !count_horizontal_flag) //if analog_in > Hi_THRESHOLD and count_flag == false
  {
    n1++;
    count_horizontal_flag = true;
  }

  if (analog_in_V <= (HI_THRESHOLD - 10)) //set count_flag to false only when below the threshold
    count_horizontal_flag = false;


  /* if(analog_in_V > HI_THRESHOLD && !count_vertical_flag) //if analog_in > Hi_THRESHOLD and count_flag == false
    {
     n2++;
     count_vertical_flag = true;
    }

    if(analog_in_V <= HI_THRESHOLD) //set count_flag to false only when below the threshold
     count_vertical_flag = false;*/

  if ((unsigned long)(currentMillis - previousMillis) >= interval && signalReceived == true)
  {
    previousMillis = currentMillis;
    sum = n1 + n2;
    Serial.print(n1);
    //Serial.write(n1);
    //Serial.print (n2);
    //Serial.print (sum);
    n1 = 0;
    n2 = 0;
    signalReceived = false;
  } else if (Serial.read() == 'y')
  {
    signalReceived = true;
    n1 = 0;
  }

  //Serial.println(analog_in_V);
}
