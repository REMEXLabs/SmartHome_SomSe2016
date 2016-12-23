<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Patient
 *
 * @ORM\Table(name="patient", indexes={@ORM\Index(name="fk_company_patient_idx", columns={"companyId"})})
 * @ORM\Entity
 */
class Patient
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
     * @ORM\Column(name="firstname", type="string", length=45, nullable=false)
     */
    private $firstname;

    /**
     * @var string
     *
     * @ORM\Column(name="lastname", type="string", length=45, nullable=false)
     */
    private $lastname;

    /**
     * @var string
     *
     * @ORM\Column(name="street", type="string", length=45, nullable=true)
     */
    private $street;

    /**
     * @var string
     *
     * @ORM\Column(name="zip", type="string", length=45, nullable=true)
     */
    private $zip;

    /**
     * @var string
     *
     * @ORM\Column(name="city", type="string", length=45, nullable=true)
     */
    private $city;

    /**
     * @var string
     *
     * @ORM\Column(name="image", type="string", length=255, nullable=true)
     */
    private $image;

    /**
     * @var integer
     *
     * @ORM\Column(name="is_asleep", type="integer", nullable=false)
     */
    private $isAsleep = '0';

    /**
     * @var \Application\Entity\Company
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Company")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="companyId", referencedColumnName="id")
     * })
     */
    private $companyid;



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
     * Set firstname
     *
     * @param string $firstname
     *
     * @return Patient
     */
    public function setFirstname($firstname)
    {
        $this->firstname = $firstname;

        return $this;
    }

    /**
     * Get firstname
     *
     * @return string
     */
    public function getFirstname()
    {
        return $this->firstname;
    }

    /**
     * Set lastname
     *
     * @param string $lastname
     *
     * @return Patient
     */
    public function setLastname($lastname)
    {
        $this->lastname = $lastname;

        return $this;
    }

    /**
     * Get lastname
     *
     * @return string
     */
    public function getLastname()
    {
        return $this->lastname;
    }

    /**
     * Set street
     *
     * @param string $street
     *
     * @return Patient
     */
    public function setStreet($street)
    {
        $this->street = $street;

        return $this;
    }

    /**
     * Get street
     *
     * @return string
     */
    public function getStreet()
    {
        return $this->street;
    }

    /**
     * Set zip
     *
     * @param string $zip
     *
     * @return Patient
     */
    public function setZip($zip)
    {
        $this->zip = $zip;

        return $this;
    }

    /**
     * Get zip
     *
     * @return string
     */
    public function getZip()
    {
        return $this->zip;
    }

    /**
     * Set city
     *
     * @param string $city
     *
     * @return Patient
     */
    public function setCity($city)
    {
        $this->city = $city;

        return $this;
    }

    /**
     * Get city
     *
     * @return string
     */
    public function getCity()
    {
        return $this->city;
    }

    /**
     * Set image
     *
     * @param string $image
     *
     * @return Patient
     */
    public function setImage($image)
    {
        $this->image = $image;

        return $this;
    }

    /**
     * Get image
     *
     * @return string
     */
    public function getImage()
    {
        return $this->image;
    }

    /**
     * Set isAsleep
     *
     * @param integer $isAsleep
     *
     * @return Patient
     */
    public function setIsAsleep($isAsleep)
    {
        $this->isAsleep = $isAsleep;

        return $this;
    }

    /**
     * Get isAsleep
     *
     * @return integer
     */
    public function getIsAsleep()
    {
        return $this->isAsleep;
    }

    /**
     * Set companyid
     *
     * @param \Application\Entity\Company $companyid
     *
     * @return Patient
     */
    public function setCompanyid(\Application\Entity\Company $companyid = null)
    {
        $this->companyid = $companyid;

        return $this;
    }

    /**
     * Get companyid
     *
     * @return \Application\Entity\Company
     */
    public function getCompanyid()
    {
        return $this->companyid;
    }
}
