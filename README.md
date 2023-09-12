# Trustedcamapp_HAW
In general, it can be expected that an untampered mobile phone records videos from the imaging chip with the correct time setting in the system time and the correct location, e.g. using GPS. Therefore, this is the phone's identity regarding imaging. In the previous "TrustedCam" project, an unaltered identity of the camera system can be taken as given as the manufactured delivers hardware and software as an unmodifiable bundle. For mobile phones, it can be easy to program an imaging app, imitating an imaging app but streaming prepared records into the "TrustedCam" system. This mimics the authenticity of a live stream, although a prerecorded stream was used. Our approach is to verify the identity of the mobile camera using different sources, including its publication on a public blockchain. Some of the functions are as follows:
•	 The raw GPS position is cross-checked with Google's augmented locating service or, e.g. ranges of IP addresses. 
•	 The identity of the mobile phone components is acquired via system information, including the ID-number of the processor and the imaging sensor. 
•	This information is written to a public blockchain returning the block number, which can be roughly converted into a time stamp. This will be cross-checked with the GPS time and the system time. 
•	After processing the information, the blockchain writes it into a block delivering an unpredictable, random block hash value. 
•	 The hash value must be printed within a limited time, e.g. 5 minutes, and recorded by the verification app. While recording it, the mobile phone creates a blinking pattern with its flashlight based on the random hash value.
 If all tests succeed, the verification extension of the "TrustedCam App" has confirmed the untampered identity of the imaging path of the mobile phone by 
•	Capturing a visual flash pattern, 
•	Produced by the app itself, 
•	Including a QR code with a unique and random block hash, 
•	which represents the serial numbers of the mobile phone components, which the app itself can cross-check, 
•	 including a time check with the block time of the transaction on the blockchain. The position is the weakest information as this can only be roughly verified with another locating service or the IP address range.
