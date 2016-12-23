<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * PatientDevices
 *
 * @ORM\Table(name="patient_devices", indexes={@ORM\Index(name="fk_device_idx", columns={"deviceId"}), @ORM\Index(name="fk_device_state_idx", columns={"state"}), @ORM\Index(name="fk_patient_pd_idx", columns={"patientId"})})
 * @ORM\Entity
 */
class PatientDevices
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="value", type="string", length=255, nullable=true)
     */
    private $value;

    /**
     * @var \Application\Entity\Device
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Device")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="deviceId", referencedColumnName="id")
     * })
     */
    private $deviceid;

    /**
     * @var \Application\Entity\DeviceState
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\DeviceState")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="state", referencedColumnName="id")
     * })
     */
    private $state;

    /**
     * @var \Application\Entity\Patient
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Patient")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="patientId", referencedColumnName="id")
     * })
     */
    private $patientid;



    /**
     * Get id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set value
     *
     * @param string $value
     *
     * @return PatientDevices
     */
    public function setValue($value)
    {
        $this->value = $value;

        return $this;
    }

    /**
     * Get value
     *
     * @return string
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * Set deviceid
     *
     * @param \Application\Entity\Device $deviceid
     *
     * @return PatientDevices
     */
    public function setDeviceid(\Application\Entity\Device $deviceid = null)
    {
        $this->deviceid = $deviceid;

        return $this;
    }

    /**
     * Get deviceid
     *
     * @return \Application\Entity\Device
     */
    public function getDeviceid()
    {
        return $this->deviceid;
    }

    /**
     * Set state
     *
     * @param \Application\Entity\DeviceState $state
     *
     * @return PatientDevices
     */
    public function setState(\Application\Entity\DeviceState $state = null)
    {
        $this->state = $state;

        return $this;
    }

    /**
     * Get state
     *
     * @return \Application\Entity\DeviceState
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * Set patientid
     *
     * @param \Application\Entity\Patient $patientid
     *
     * @return PatientDevices
     */
    public function setPatientid(\Application\Entity\Patient $patientid = null)
    {
        $this->patientid = $patientid;

        return $this;
    }

    /**
     * Get patientid
     *
     * @return \Application\Entity\Patient
     */
    public function getPatientid()
    {
        return $this->patientid;
    }
}
