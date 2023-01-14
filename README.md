# Skin Diseases Detector
<b>The main idea is to provides an instant service to detect the skin diseases that the patient suffer from. </b> <br />
The application enable the user to upload a photo from the gallery or take a photo directly for the skin surface of the injury.<br />
It uses CNN network (MobileNet V2) modified using “transfer learning” to detect the disease.<br />
The network has been trained on a relative small data set, but the my modifications applied in the network design compensated that which obtained very good accuracy (∝96%).
Multiple services after the detection has been implemented:
- Details:
  Provides a general view of the diagnosed disease.
- Medication:
  Provides the best medicine for the diagnosed diseases.
- Contact a Doctor:
  Send Email or an SMS to the doctor of the patient.<br />
  The application provides the ability to enter the doctor info (email, phone number and name) previously.
- The application has 'About' and 'Diseases' activities that provide an overview of the application and the supported diseases respectively.
