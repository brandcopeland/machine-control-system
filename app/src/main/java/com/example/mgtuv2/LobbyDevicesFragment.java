package com.example.mgtuv2;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Класс фрагмента списка доступа к девайсам в активности лобби
public class LobbyDevicesFragment extends Fragment {

    View view;
    Context context;
    TextView textViewDevices;
    RecyclerView recyclerView;

    public List<DeviceUnit> deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lobby_devices, container, false);
        textViewDevices = view.findViewById(R.id.textViewDevices);
        recyclerView = view.findViewById(R.id.devicesList);
        context = getContext();

        updateDevicesList();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MyAdapter(context,deviceList));

        return view;
    }


    //Convert JSON String with QrCode and Timestamps to class variables
    public void setDevicesFromJSON(List<DeviceUnit> list, String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            System.out.println(jsonObject.length());
            JSONArray devices = jsonObject.names();
            if (devices != null) {
                for (int i = 0; i < devices.length(); i++) {
                    String device = devices.getString(i);
                    JSONObject innerObject = jsonObject.getJSONObject(device);
                    String shortName = innerObject.getString("short_name");
                    String description = innerObject.getString("description");

                    list.add(new DeviceUnit(shortName,description,1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDevicesList(){
        deviceList = new ArrayList<DeviceUnit>();
        setDevicesFromJSON(deviceList, djangoUser.getReceivedDevicesList());
    }


    //Класс одного станка, содержит имя, описание и изображение(Временно помечено как int)
    public class DeviceUnit {

        String name;
        String description;
        int image;

        public DeviceUnit(String name, String description, int image) {
            this.name = name;
            this.description = description;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }
    }


    //Класс отображения станка в RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView, descriptionView;

        public MyViewHolder(@NonNull View DeviceUnitView) {
            super(DeviceUnitView);
            imageView = DeviceUnitView.findViewById(R.id.imageView);
            nameView = DeviceUnitView.findViewById(R.id.deviceName);
            descriptionView = DeviceUnitView.findViewById(R.id.deviceDescription);
        }
    }

    //Класс адаптера для RecyclerView
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        Context context;
        List<DeviceUnit> devices;

        public MyAdapter(Context context, List<DeviceUnit> devices) {
            this.context = context;
            this.devices = devices;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.device_view,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
            holder.nameView.setText(devices.get(position).getName());
            holder.descriptionView.setText(devices.get(position).getDescription());
            holder.imageView.setImageResource(R.drawable.baseline_developer_board_24);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }
}