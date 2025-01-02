An Architecture Combining Convolutional Neural Network (CNN) and Linear Support Vector Machine (SVM) for Image Classification


*This project was inspired by Y. Tang's [Deep Learning using Linear Support Vector Machines](https://arxiv.org/abs/1306.0239)
(2013).*


## Usage

First, clone the project.
```bash
git clone https://github.com/AFAgarap/cnn-svm.git/
```

Run the `setup.sh` to ensure that the pre-requisite libraries are installed in the environment.
```bash
sudo chmod +x setup.sh
./setup.sh
```

Program parameters.
```bash
usage: main.py [-h] -m MODEL -d DATASET [-p PENALTY_PARAMETER] -c
               CHECKPOINT_PATH -l LOG_PATH

CNN & CNN-SVM for Image Classification

optional arguments:
  -h, --help            show this help message and exit

Arguments:
  -m MODEL, --model MODEL
                        [1] CNN-Softmax, [2] CNN-SVM, [3] CNN-SVM (low hyper-params) , [4] CNN-Softmax (low hyper-params), [5] CNN-Softmax (slightly higher hyper-params), [6] CNN-SVM (slightly higher hyper-params)
  -d DATASET, --dataset DATASET
                        path of the MNIST dataset
  -p PENALTY_PARAMETER, --penalty_parameter PENALTY_PARAMETER
                        the SVM C penalty parameter
  -c CHECKPOINT_PATH, --checkpoint_path CHECKPOINT_PATH
                        path where to save the trained model
  -l LOG_PATH, --log_path LOG_PATH
                        path where to save the TensorBoard logs
```

Then, go to the repository's directory, and run the `main.py` module as per the desired parameters.
```bash
cd cnn-svm
python3 main.py --model 2 --dataset ./MNIST_data --penalty_parameter 1 --checkpoint_path ./checkpoint --log_path ./logs
```
