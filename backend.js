const express = require('express');
const path = require('path'); 
const bodyParser = require('body-parser');
const OAuthToken = require('./ebay_oauth_token'); 
const cors = require('cors');
const axios = require('axios'); // 引入 axios
const app = express();
const client_id = "xxx"  ;
const client_secret = "xxx"  ;
const mongoose=require('mongoose');
const Wishlist=require('./Wishlist_HW4');
// Middleware
app.use(cors());
app.use(bodyParser.json()); 
const PORT = process.env.PORT || 3000;
const dbURI='xxxx';

mongoose.connect(dbURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then((result) => {
    app.listen(PORT, () => {
      console.log(`Server started on port ${PORT}`);
   });
  })
  .catch((err) => console.log(err));

 
app.use(express.static('dist'));
app.get('/sendreqgen', async (req, res) => {
  const url = req.query.url;
  console.log('Received URL:', url);
  const response = await axios.get(url);
  res.json(response.data);
});

app.get('/getPostalCode', async (req, res) => {
  try {
      const query = decodeURIComponent(req.query.query);
      console.log('query is',query);
      const url = `http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith=${query}&maxRows=5&username=peiyi_usc&country=US`;
      
      const response = await axios.get(url);
      console.log(response);
      const postalCodes = response.data.postalCodes.map(item => item.postalCode);
    console.log('send response');
      res.json(postalCodes);
     
  } catch (error) {
      res.status(500).send('Internal Server Error');
  }
});
app.get('/detail', async (req, res) => {
  try {
    console.log('收到detail请求');
    const itemID = req.query.itemID; 
    let detailUrl = `https://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=xxx&siteid=0&version=967&ItemID=${itemID}&IncludeSelector=ShippingCosts,Description,Details,ItemSpecifics`;
    console.log("detailURL",detailUrl);
    const oauthToken = new OAuthToken(client_id, client_secret);
    const accessToken = await oauthToken.getApplicationToken();
    //console.log('Access Token:', accessToken);

   
    const response = await axios.get(detailUrl, {
      headers: {
        'X-EBAY-API-IAF-TOKEN': accessToken, 
        'Content-Type': 'application/json'
      }
    });

   
    const detailData = response.data;
    res.json(detailData);

  } catch (error) {
    console.error('Error:', error.message);
    res.status(500).send('Internal Server Error');
  }
});
app.get('/phototab',async(req,res)=>{
  console.log('收到photo请求');
  title = req.query.title;
  let url="https://www.googleapis.com/customsearch/v1?q=";
    url+=title; 
    url+="&cx=f08640602415c41f2";
    url+="&imgSize=huge&num=8&searchType=image&key=xxx";
    console.log(url);
  console.log('photocome');
  try {
    const response = await axios.get(url);
    res.json(response.data);
  } catch (error) {
    console.error('Error fetching data:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
  
  
});
app.get('/similarproducts',async(req,res)=>{
  console.log('similarproduct was called');
  id = req.query.itemID;
  let url=`https://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=`;
    url+=`xxx`; 
    url+=`&RESPONSE-DATA-FORMAT=JSON&REST-AYLOAD&itemId=`;
    url+=id;
    url+=`&maxResults=20`;
  console.log('similarproduct come');
  console.log(url);
  const response = await axios.get(url);
  res.json(response.data);
});

//添加單一物件到mongodb
app.post('/addto', async (req, res) => {
  try {
    const productinfo = req.body;
    const newWishlistItem = new Wishlist({
      productid:productinfo.productid,
      image: productinfo.image,
      title: productinfo.title,
      price: productinfo.price,
      shippingcost: productinfo.shippingcost,
      condition: productinfo.condition,
      zipcode:productinfo.zipcode
    });

    const result = await newWishlistItem.save();
    console.log(result,'有存到');
    res.send(result);
  } catch (error) {
    console.error("Error adding product to wishlist:", error);
    res.status(500).send({ message: "Failed to add product to wishlist." });
  }
});

//刪掉單一物件
app.delete('/delete/:productid', async (req, res) => {
  try {
    const { productid } = req.params; 
    const result = await Wishlist.deleteOne({ productid: productid });
    
    if (result.deletedCount === 1) {
      res.send({ message: "Product successfully deleted" });
    } else {
      res.status(404).send({ message: "Product not found" });
    }
  } catch (error) {
    console.error("Error deleting product:", error);
    res.status(500).send({ message: "Failed to delete product." });
  }
});
//obtain all in mongodb
app.get('/getalllist', async (req, res) => {
  try {
    const data = await Wishlist.find();
    res.send({ data: data, message: "get back all" });
  } catch (error) {
    console.error("error in getback:", error);
    res.status(500).send({ message: "Error fetching data." });
  }
});



